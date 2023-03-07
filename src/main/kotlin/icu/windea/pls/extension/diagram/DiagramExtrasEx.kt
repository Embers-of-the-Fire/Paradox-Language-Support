package icu.windea.pls.extension.diagram

import com.intellij.diagram.*
import com.intellij.diagram.components.*
import com.intellij.diagram.extras.custom.*
import com.intellij.openapi.graph.view.*
import com.intellij.psi.*
import com.intellij.ui.*
import com.intellij.util.ui.JBUI.*
import icu.windea.pls.core.*
import java.awt.*
import javax.swing.*

private val myItemComponentField by lazy { DiagramNodeBodyComponent::class.java.getDeclaredField("myItemComponent").apply { trySetAccessible() } }
private val myLeftField by lazy { DiagramNodeItemComponent::class.java.getDeclaredField("myLeft").apply { trySetAccessible() } }
private val myRightField by lazy { DiagramNodeItemComponent::class.java.getDeclaredField("myRight").apply { trySetAccessible() } }

abstract class DiagramExtrasEx : CommonDiagramExtras<PsiElement>() {
    override fun createNodeComponent(node: DiagramNode<PsiElement>, builder: DiagramBuilder, nodeRealizer: NodeRealizer, wrapper: JPanel): JComponent {
        //允许添加自定义的组件
        val component = super.createNodeComponent(node, builder, nodeRealizer, wrapper)
        if(component is DiagramNodeContainer) {
            val nodeBodyComponent = component.nodeBodyComponent
            myItemComponentField.set(nodeBodyComponent, DiagramNodeItemComponentEx())
        }
        return component
    }
}

//com.intellij.diagram.components.DiagramNodeContainer
//com.intellij.diagram.components.DiagramNodeBodyComponent
//com.intellij.diagram.components.DiagramNodeItemComponent

class DiagramNodeItemComponentEx : DiagramNodeItemComponent() {
    private var useComponent = false
    
    //使用自定义组件时myLeft和myRight的宽度应当为0
    
    init {
        val left = object : SimpleColoredComponent() {
            override fun getPreferredSize() = super.getPreferredSize().alsoIf(useComponent) { it.width = 0 }
        }
        val right = object : SimpleColoredComponent() {
            override fun getPreferredSize() = super.getPreferredSize().alsoIf(useComponent) { it.width = 0 }
        }
        myLeftField.set(this, left)
        myRightField.set(this, right)
        removeAll()
        add(left, BorderLayout.WEST)
        add(right, BorderLayout.EAST)
        left.isOpaque = true
        left.isIconOpaque = true
        right.isOpaque = true
        right.isIconOpaque = true
        this.isOpaque = true
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun setUp(owner: DiagramNodeBodyComponent, builder: DiagramBuilder, node: DiagramNode<Any>, element: Any?, selected: Boolean) {
        super.setUp(owner, builder, node, element, selected)
        val elementManager = builder.provider.elementManager as DiagramElementManager<Any>
        if(elementManager is DiagramElementManagerEx) {
            if(components.size == 3) {
                remove(2)
            }
            val nodeElement = node.identifyingElement
            val component = elementManager.getItemComponent(nodeElement, element, builder)
            if(component != null) {
                add(component)
                useComponent = true
            } else {
                useComponent = false
            }
            this.size = this.preferredSize
        }
    }
}