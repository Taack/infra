package taack.ssh.vfs

interface INode {
    String getName()
    void setParent(INode parent)
    INode getParent()
    void addChild(INode node)
    Iterator<INode> getChildren()
}