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
        <nk-doc-link :doc="value"></nk-doc-link>
    </span>
    <div v-else>
        <a-input size="small"
                 :read-only="true"
                 @click="docSelectModalVisible=true"
                 style="cursor: pointer; max-width: 300px;"
                 :value="value && value.docName"
        ></a-input>
        <nk-doc-select-modal v-model="docSelectModalVisible"
                             :modal="inputOptions.optionsObject"
                             @select="docSelected"
        ></nk-doc-select-modal>
    </div>
</template>

<script>
export default {
    props:{
        value: {},
        editMode: {
            type: Boolean,
            default:false
        },
        inputOptions: {
            type:Object,
            default(){
                return {}
            }
        }
    },
    data(){
        return {
            docSelectModalVisible:false,
        }
    },
    methods:{
        docSelected(e){
            const value = Object.assign(this.value||{},{
                docId:e.docId,
                docName:e.docName
            });
            this.$emit('input', value);
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>