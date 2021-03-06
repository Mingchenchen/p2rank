package cz.siret.prank.utils

import com.google.common.base.Function
import groovy.transform.CompileStatic

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Collection utils
 */
@CompileStatic
class Cutils {

    static boolean empty(Collection<?> col) {
        return col == null || col.isEmpty()
    }

//    static boolean empty(List col) {
//        return col == null || col.isEmpty()
//    }

    static <T> List<T> head(int n, List<T> list) {
        if (n>=list.size()) return list
        return list.subList(0, n)
    }

    static <T> List<T> tail(int n, List<T> list) {
        return head(n, list.reverse())
    }


    static <K, E> Map<K, E> transformKeys(Map<K, E> map, Closure<K> closure) {
        Map<K, E> res = new HashMap<>(map.size())
        map.each {
            res.put( closure.call(it.key), it.value )
        }
        return res
    }

    static <E> Map<String, E> prefixMapKeys(Map<String, E> map, String prefix) {
        transformKeys(map, { (String) prefix + it})
    }

    static double sum(List<Double> list) {
        if (list == null || list.empty) return 0

        double sum = 0
        for (Double d : list) {
            if (d != null) {
                sum += d
            }
        }

        return sum
    }

    static <K, V> Map<K, V> mapWithIndex(
            Iterable<V> values, Function<? super V, K> keyFunction) {
        return mapWithIndex(values.iterator(), keyFunction);
    }

    static <K, V> Map<K, V> mapWithIndex(
            Iterator<V> values, Function<? super V, K> keyFunction) {

        values.collectEntries {
            [(keyFunction.apply(it)): it]
        }
    }

    static <E> List<E> findDuplicates(Iterable<E> values) {
        values.groupBy{ it }.values().findAll { it.size() > 1}.collect { it[0] }.toList()
    }

    /**
     * get element or null
     */
    @Nullable
    static <E> E listElement(int idx, List<E> list) {
        if (list == null) return null
        if (idx < 0 || idx >= list.size()) return null
        return list.get(idx)
    }

    @Nonnull
    static <E> List<E> newSynchronizedList() {
        return Collections.synchronizedList(new ArrayList<E>());
    }

    @Nonnull
    static <E> List<E> newSynchronizedList(int initialSize) {
        return Collections.synchronizedList(new ArrayList<E>(initialSize));
    }

    @Nonnull
    static <E> List<E> synchronizedCopy(Collection<E> collection) {
        return Collections.synchronizedList(new ArrayList<E>(collection));
    }

}
