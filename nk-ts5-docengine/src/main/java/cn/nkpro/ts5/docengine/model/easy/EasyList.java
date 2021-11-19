package cn.nkpro.ts5.docengine.model.easy;

import cn.nkpro.ts5.exception.NkOperateNotAllowedCaution;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EasyList implements EasyCollection<EasySingle> {

    private List<Object> target;
    List<EasySingle> easies;

    EasyList(List<Object> target) {
        this.target = target;
        this.easies = this.target.stream().map(EasySingle::from).collect(Collectors.toList());
    }

    EasyList() {}

    @Override
    public EasySingle append(int index){
        index = index==-1?easies.size():index;
        Map<Object,Object> t = new HashMap<>();
        EasyMap s = new EasyMap(t);
        easies.add(index,s);
        target.add(index,t);
        return s;
    }
    @Override
    public EasySingle append(){
        return append(-1);
    }

    @Override
    public EasySingle find(Function<EasySingle, Boolean> function){
        return easies.stream()
                .filter(function::apply)
                .findFirst()
                .orElse(null);
    }

    @Override
    public EasySingle get(int index){
        return easies.get(index);
    }

    @Override
    public boolean add(EasySingle e) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends EasySingle> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public boolean remove(Object o) {
        if(o instanceof EasySingle){
            target.remove(((EasySingle) o).target());
            return easies.remove(o);
        }
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public int size() {
        return easies.size();
    }

    @Override
    public boolean isEmpty() {
        return easies.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return easies.contains(o);
    }

    @NotNull
    @Override
    public Iterator<EasySingle> iterator() {
        return easies.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return easies.toArray();
    }

    @SuppressWarnings("all")
    @Override
    public <T> T[] toArray(T[] a) {
        return easies.toArray(a);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return easies.containsAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public void clear() {
        target.clear();
        easies.clear();
    }
}
