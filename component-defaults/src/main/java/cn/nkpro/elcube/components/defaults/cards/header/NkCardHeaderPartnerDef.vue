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
    <a-card :title="`${card.cardName}配置`">
        <nk-edit-slot :edit-mode="editMode" :options="{style:{width:'100%'}}">
            {{def}}
            <a-input slot="edit" type="textarea" v-model="json"></a-input>
        </nk-edit-slot>
    </a-card>
</template>

<script>
    import MixinDef from "MixinDef";

    export default {
    mixins:[new MixinDef()],
    computed:{
        json:{
            get(){
                return JSON.stringify(this.def);
            },
            set(value){
                try{
                    this.def = JSON.parse(value);
                }catch (e){
                    console.warn(e)
                    // eslint-disable-line
                }
            }
        }
    },
    created() {
    }
}
</script>

<style scoped>

</style>