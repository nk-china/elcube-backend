<!--
	This file is part of EAsis.
	EAsis is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	EAsis is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
-->
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