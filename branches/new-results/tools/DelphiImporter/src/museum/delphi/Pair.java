/**
 * A generic-typed pair of objects.
 * @author pschmitz
 */
package museum.delphi;

public class Pair<F,S> {
  F first;
  S second;

  public F getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  public void setFirst(F pFirst) {
    first = pFirst;
  }

  public void setSecond(S pSecond) {
    second = pSecond;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pair)) return false;

    final Pair pair = (Pair) o;

    if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
    if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (first != null ? first.hashCode() : 0);
    result = 29 * result + (second != null ? second.hashCode() : 0);
    return result;
  }

  public String toString() {
    return "(" + getFirst() + ", " + getSecond() + ")";
  }

  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }
}
