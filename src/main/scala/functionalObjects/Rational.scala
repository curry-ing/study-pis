package functionalObjects

class Rational(n: Int, d: Int) {

  require(d != 0)

  private val g = gcd(n.abs, d.abs)
  val numerator: Int = n / g
  val denominator: Int = d / g

  def this(n: Int) = this(n, 1)

  override def toString: String = numerator + "/" + denominator

  private def gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a % b)

  def +(that: Rational): Rational = {
    new Rational(
      this.numerator * that.denominator + that.numerator * this.denominator,
      this.denominator * that.denominator)
  }

  def *(that: Rational): Rational = {
    new Rational(
      this.numerator * that.numerator,
      this.denominator * that.denominator
    )
  }

  def lessThan(that: Rational): Boolean =
    this.numerator * that.denominator < that.numerator * this.denominator

  def max(that: Rational): Rational =
    if (this lessThan that) that else this
}
