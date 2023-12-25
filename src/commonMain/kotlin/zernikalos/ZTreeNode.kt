package zernikalos

interface ZTreeNode<T: ZTreeNode<T>> {

    var parent: T?
    val hasParent: Boolean

    val children: Array<T>
}