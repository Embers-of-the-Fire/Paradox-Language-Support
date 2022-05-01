package icu.windea.pls.tool

import co.phoenixlab.dds.*
import com.google.common.cache.*
import icu.windea.pls.*
import org.slf4j.*
import java.lang.invoke.*
import java.nio.file.*
import kotlin.io.path.*

/**
 * DDS文件到PNG文件的转化器。
 *
 * 基于[DDS4J](https://github.com/vincentzhang96/DDS4J)。
 */
@Suppress("unused")
object DdsToPngConverter {
	private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
	
	private val ddsImageDecoder: DdsImageDecoder by lazy { DdsImageDecoder() }
	private val ddsCache: Cache<String, Path> by lazy { createCache() } //ddsAbsPath - pngAbsPath
	private val externalDdsCache: Cache<String, Path> by lazy { createCache() } //ddsAbsPath - pngAbsPath
	
	//TODO 需要检查DDS文件被更改的情况
	
	/**
	 * 将DDS文件转化为PNG文件，返回PNG文件的绝对路径。如果发生异常，则返回null。
	 * @param ddsAbsPath DDS文件的绝对路径。
	 * @param ddsRelPath DDS文件相对于游戏或模组根路径的路径（如果可以获取）。
	 */
	fun convert(ddsAbsPath: String, ddsRelPath: String? = null): String? {
		try {
			//如果存在基于DDS文件绝对路径的缓存数据，则使用缓存的PNG文件绝对路径
			val pngAbsPath = getPngAbsPath(ddsAbsPath, ddsRelPath)
			if(pngAbsPath.notExists()) {
				doConvertDdsToPng(ddsAbsPath, pngAbsPath)
			}
			return pngAbsPath.absolutePathString()
		} catch(e: Exception) {
			logger.warn(e) { "Convert dds image to png image failed. (dds absolute path: $ddsAbsPath, dds relative path: $ddsRelPath)" }
			return null
		}
	}
	
	private fun getPngAbsPath(ddsAbsPath: String, ddsRelPath: String?): Path {
		val cache = if(ddsRelPath != null) ddsCache else externalDdsCache
		return cache.get(ddsAbsPath) { doGetPngAbsPath(ddsAbsPath, ddsRelPath) }
	}
	
	private fun doGetPngAbsPath(ddsAbsPath: String, ddsRelPath: String?): Path {
		val pngAbsPath = doGetRelatedPngPath(ddsAbsPath, ddsRelPath)
		doConvertDdsToPng(ddsAbsPath, pngAbsPath)
		return pngAbsPath
	}
	
	private fun doGetRelatedPngPath(ddsAbsPath: String, ddsRelPath: String?): Path {
		if(ddsRelPath != null) {
			//路径：~/.pls/images/${uuid}/${ddsRelPath}.png
			val uuid = ddsAbsPath.removePrefix(ddsRelPath).toUUID().toString() //得到基于游戏或模组目录的绝对路径的UUID
			return PlsPaths.imagesDirectoryPath.resolve("$uuid/$ddsRelPath.png") //直接在包括扩展名的DDS文件名后面加上".png"
		} else {
			//路径：~/.pls/images/external/${uuid}/${ddsFileName}.png
			val index = ddsAbsPath.lastIndexOf('/')
			val parent = if(index == -1) "" else ddsAbsPath.substring(0, index)
			val fileName = if(index == -1) ddsAbsPath else ddsAbsPath.substring(index + 1)
			val uuid = if(parent.isEmpty()) "" else parent.toUUID().toString() //得到基于DDS文件所在目录的UUID
			return PlsPaths.imagesDirectoryPath.resolve("external/$uuid.$fileName.png") //直接在包括扩展名的DDS文件名后面加上".png"
		}
	}
	
	private fun doConvertDdsToPng(ddsAbsPath: String, pngAbsPath: Path) {
		val dds = Dds()
		dds.read(Files.newByteChannel(ddsAbsPath.toPath(), StandardOpenOption.READ))
		pngAbsPath.create()
		val outputStream = Files.newOutputStream(pngAbsPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
		ddsImageDecoder.convertToPNG(dds, outputStream)
		outputStream.flush()
		outputStream.close()
	}
	
	fun getUnknownPngPath(): String {
		if(PlsPaths.unknownPngPath.notExists()) {
			val url = "/${PlsPaths.unknownPngName}".toUrl(locationClass)
			Files.copy(url.openStream(), PlsPaths.unknownPngPath) //将jar包中的unknown.png复制到~/.pls/images中
		}
		return PlsPaths.unknownPngPath.toString()
	}
}