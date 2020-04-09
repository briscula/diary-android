package press.util

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

/**
 * Pauses items from upstream while predicate is true.
 */
fun <T : Any, O : Any> Observable<T>.suspendWhile(predicateArgProvider: Observable<O>, predicate: (O) -> Boolean): Observable<T> {
  val combineLatest = Observables.combineLatest(this, predicateArgProvider);
  return combineLatest
          .filter { (_, predicateValue) -> predicate(predicateValue).not() }
          .map { (upstreamItem) -> upstreamItem }
}
