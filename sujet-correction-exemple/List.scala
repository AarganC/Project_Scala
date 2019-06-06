abstract class List[+A]
case object Empty extends List[Nothing]
case class Cons[A](h:A, t:List[A]) extends List[A]

object List {
    def foldRight[A, B](as:List[A], b:B, f:(A, B) => B):B = as match {
        case Empty => b
        case Cons(h, t) => f(h, foldRight(t, b, f))
    }
    def foldLeft[A, B](as:List[A], b:B, f:(B, A) => B):B = as match {
        case Empty => b
        case Cons(h, t) => foldLeft(t, f(b, h), f)
    }
    def reduceRight[A](as:List[A], f:(A, A) => A):A = as match {
        case Empty => error("bzzt. reduceRight on empty list")
        case Cons(h, t) => foldRight(t, h, f)
}
    def reduceLeft[A](as:List[A], f:(A, A) => A):A = as match {
        case Empty => error("bzzt. reduceLeft on empty list")
        case Cons(h, t) => foldLeft(t, h, f)
} 

def sum[Int](as: List[Int]):Int = foldLeft(as, 0, (acc:Int, e:Int) => acc + e)

def length[A](as:List[A]):Int = foldRight(as, 0, (e: A, acc: Int) => acc +1)

def map[A, B](as:List[A], f:A => B):List[B] = foldRight(as, Empty, 
            (e: A, acc:List[B]) => Cons(f(e), acc)
            )


def filter[A](as: List[A], f: A => Boolean): List[A] = {
    foldRight(as, Empty, (e: A, acc:List[A]) => if(f(e)){
        Cons(e, acc)
    }
    else {
        acc
    })
}

def concat[A](x:List[A], y:List[A]):List[A] = {
    foldRight(x, y, (e, acc) => Cons(e, acc))
}

/* x = Cons(5,Cons(6, Empty)) et y = Cons(7,Cons(8, Empty))
1 : e = 6 , acc = Cons(7,Cons(8, Empty)) => Cons(6, Cons(7, Cons(8, Empty)))
2 : e = 5 , acc = Cons(6, Cons(7, Cons(8, Empty))) => Cons(5, Cons(6, Cons(7, Cons(8, Empty))))
*/


def flatten[A](as:List[List[A]]):List[A] = reduceLeft(as, 
                                (l1: List[A], l2:List[A]) => concat(l1, l2)
                                )

def flatMap[A, B](as:List[A], f:A => List[B]):List[B] = flatten(map(as, f)) // List[B]

// is List de Int >= 0
def maximum(is:List[Int]):Int = foldLeft(is, 0, (acc: Int, e: Int) => if(e > acc){ // pour minimum (e < acc)
        e
    }
    else{
        acc
    })

def reverse[A](as:List[A]):List[A] = foldLeft(as, Empty, (acc :List[A], e :A) => Cons(e, acc))


}