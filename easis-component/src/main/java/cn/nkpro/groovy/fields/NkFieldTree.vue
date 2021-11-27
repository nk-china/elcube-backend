<template>
    <span v-if="!editMode">{{value | formatTree(inputOptions.optionsObject)}}</span>
    <a-tree-select size="small"
              v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :tree-data="inputOptions.optionsObject"
               tree-checkable
               :show-checked-strategy="SHOW_PARENT"
              @change="change">
    </a-tree-select>
</template>

<script>
import {TreeSelect} from 'ant-design-vue';


const findInTree = (tree,value)=>{
    for(let i in tree){
        const item = tree[i];
        if(item.key === value || item.value === value){
            return item;
        }
        if(item.children){
            let ret = findInTree(item.children, value);
            if(ret){
                return ret;
            }
        }
    }
    return undefined;
};

export default {
    props:{
        editMode: Boolean,
        value: {},
        inputOptions: {
            type:Object,
            default(){
                return {}
            }
        }
    },
    filters:{
        formatTree(value, options){
            if(value) {
                options = typeof options === 'string' ? JSON.parse(options) : options;
                return (Array.isArray(value) ? value : [value]).map(item => {
                    let find = options && findInTree(options, item);
                    if (find) {
                        return find.label || find.name || find.title;
                    }
                    return item;
                }).join(' , ');
            }
        }
    },
    data(){
        return {
            SHOW_PARENT:TreeSelect.SHOW_PARENT,
        }
    },
    computed:{
        val:{
            get(){
                return this.value;
            },
            set(value){
                this.$emit('input',value);
            }
        }
    },
    methods:{
        change(){
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>