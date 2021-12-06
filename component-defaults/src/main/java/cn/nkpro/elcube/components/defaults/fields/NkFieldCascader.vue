<!--
	This file is part of ELCube.
	ELCube is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCube is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <span v-if="!editMode">{{value | formatCascader(inputOptions.optionsObject)}}</span>
    <a-cascader size="small"
              v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :options="inputOptions.optionsObject"
              @change="change">
    </a-cascader>
</template>

<script>
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
        formatCascader(value, options){
            if(value) {
                options = typeof options === 'string' ? JSON.parse(options) : options;
                let find = options;
                return value.map(item => {
                    find = find && find.find(o => o.key === item || o.value === item);
                    if (find) {
                        const label = find.label || find.name || find.title;
                        find = find.children;
                        return label;
                    }
                    return item;
                }).join(' / ');
            }
        },
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