/*object FC {


  // A mettre dans un package diff des exos d'avant
  case class User(name: String, address: String)
  val users = List(
    User("toto", "rtyui"),
    User("tata", "fghjkl"),
    User("tutu", "fghjkl")
  )

  def addressOf(users: List[User]): List[String] = users.map(x => x.address) // map(_.address)


  case class Employee(name: String, salary: Double)
  val employees = List(
    Employee("toto", 45678D),
    Employee("tata", 2345678D)
  )

  def salarySum(employees: List[Employee]): Double = employees.reduce((x1, x2) => x1.salary + x2.salary)
  //employees.map(_.salary).sum
  //foldLeft(0D)((acc, e) => acc + e.salary)

  // Option[Int] => Some(3) ou None

  def add(o1: Option[Int], o2: Option[Int]): Option[Int] = //o1.flatMap(x => o2.map(y => x+y))
    for {
      v1 <- o1
      v2 <- o2
    } yield v1 + v2

  def average(values: Iterator[Double]): Double = {
    val res = values.foldLeft((0D, 0)){
      (acc, e) =>{
        val sum = acc._1
        val nb = acc._2
        (sum+e, nb+1)
      }}
    res._1 / res._2
  }

  def average(values: Iterator[Double]): Double = values.sum / values.length

  // For comprehension
  case class Book(title: String, authors: List[String])

  val books: List[Book] = List(
    Book("Structure and Interpretation of Computer Programs",
      List("Abelson, Harold", "Sussman, Gerald J.")),
    Book("Principles of Compiler Design",
      List("Aho, Alfred", "Ullman, Jeffrey")),
    Book("Programming in Modula-2",
      List("Wirth, Niklaus")),
    Book("Introduction to Functional Programming",
      List("Bird, Richard")),
    Book("The Java Language Specification",
      List("Gosling, James", "Joy, Bill", "Steele, Guy", "Bracha, Gilad")),
    Book("Another Book",
      List("Gosling, James", "Wirth, Niklaus", "Sussman, Gerald J.")))

  def findBooksWithAuthor(books: List[Book], author: String): List[String] = {
    for{
      b <- books
      a <- b.authors if a.startsWith(author)
    } yield b.title
  }

  def findBookWithName(books: List[Book], name: String): List[String] = {
    for{
      b <- books if b.title.contains(name)
    } yield b.title
  }

  def greatsAuthor(books: List[Book]): List[String] = {
    for {
      b1 <- books
      b2 <- books if b1 != b2
      a1 <- b1.authors
      a2 <- b2.authors if a1 == a2
    } yield a1
  }

  def deduplicates[A](list: List[A]): List[A] = {
    list.foldLeft(List[A]())(
      (acc, e) => if(acc.contains(e)){
        acc
      }
      else {
        acc :+ e
      }
    )
  }
}
*/