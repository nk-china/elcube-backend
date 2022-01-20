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
    <span v-if="!editMode">
        <template v-if="inputOptions.tagColor">
            <a-tag v-for="(item,index) in list" :key="index" :color="inputOptions.tagColor">{{item | nkFromList(inputOptions.optionsObject)}}</a-tag>
        </template>
        <template v-else>
            {{value | nkFromList(inputOptions.optionsObject)}}
        </template>
    </span>

    <a-checkbox-group
            size="small"
            v-else-if="inputOptions.selectMode==='checkbox'"
            v-model="val"
            :options="inputOptions.optionsObject"
            @change="change">
    </a-checkbox-group>
    <a-radio-group
            size="small"
            v-else-if="inputOptions.selectMode==='radio'"
            v-model="val"
            :options="inputOptions.optionsObject"
            @change="change">
    </a-radio-group>
    <a-select size="small"
              v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :options="inputOptions.optionsObject"
              @change="change">
    </a-select>
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
    computed:{
        list(){
            if(this.value){
                return typeof this.value === 'object' && this.value[0] ? this.value : [this.value];
            }
        },
        val:{
            get(){
                if(this.inputOptions.selectMode==='checkbox' || this.inputOptions.selectMode==='multiple'){
                    if(!this.value || !this.value instanceof Array){
                        return [];
                    }
                }
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