import Tree._
import org.scalatest.{FlatSpec, Matchers}

class TreeSpec extends FlatSpec with Matchers{
  val myTree = Branch(1, Branch(1, Leaf(2), Leaf(4)), Branch(5, Leaf(2), Leaf(3)))

  "Size" should "gives the size of Tree" in {
    Tree.size(myTree) shouldEqual 7
    Tree.size(Leaf(2)) shouldEqual 1
  }

  "Max" should "gives us the max value in the Tree" in {
    Tree.max(myTree) shouldEqual 5
    Tree.max(Leaf(3)) shouldEqual 3
  }

  "Min" should "gives us the min value in the Tree" in {
    Tree.min(myTree) shouldEqual 1
    Tree.min(Leaf(3)) shouldEqual 3
  }

  "Depth" should "gives us the depth of the Tree" in {
    Tree.depth(myTree) shouldEqual 3
    Tree.depth(Leaf(2)) shouldEqual 1
  }

  "Leaves" should "gives a list of leaves" in {
    Tree.leaves(myTree) shouldEqual List(Leaf(2), Leaf(4), Leaf(2), Leaf(3))
    Tree.leaves(Leaf(1)) shouldEqual List(Leaf(1))
  }
}
