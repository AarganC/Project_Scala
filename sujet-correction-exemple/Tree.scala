sealed trait Tree
case class Leaf(value: Int) extends Tree
case class Branch(value: Int, left: Tree, right: Tree) extends Tree

object Tree {
  def size(t: Tree): Int = {
    if(t.isInstanceOf[Leaf]){
      1
    }
    else {
      size(t.asInstanceOf[Branch].left) + size(t.asInstanceOf[Branch].right) + 1
    }
  }

  def max(t: Tree): Int = {
    if(t.isInstanceOf[Leaf]){
      t.asInstanceOf[Leaf].value
    }
    else {
      max(t.asInstanceOf[Branch].left) max max(t.asInstanceOf[Branch].right) max t.asInstanceOf[Branch].value
    }
  }


  def min(t: Tree): Int = {
    if(t.isInstanceOf[Leaf]){
      t.asInstanceOf[Leaf].value
    }
    else {
      min(t.asInstanceOf[Branch].left) min min(t.asInstanceOf[Branch].right) min t.asInstanceOf[Branch].value
    }
  }

  def depth(t: Tree): Int = {
    if(t.isInstanceOf[Leaf]){
      1
    }
    else {
      (depth(t.asInstanceOf[Branch].left) + 1) max (depth(t.asInstanceOf[Branch].right) + 1)
    }
  }

  def isBalanced(t: Tree): Boolean = {
    ???
  }

  def isSymetric(t: Tree): Boolean = {
    ???
  }

  def nbLeaves(t: Tree): Int = {
    if(t.isInstanceOf[Leaf]){
      1
    }
    else {
      nbLeaves(t.asInstanceOf[Branch].left) + nbLeaves(t.asInstanceOf[Branch].right)
    }
  }

  def leaves(t: Tree): List[Leaf] = {
    if(t.isInstanceOf[Leaf]){
      List(t.asInstanceOf[Leaf])
    }
    else {
      leaves(t.asInstanceOf[Branch].left) ++ leaves(t.asInstanceOf[Branch].right)
    }
  }
}