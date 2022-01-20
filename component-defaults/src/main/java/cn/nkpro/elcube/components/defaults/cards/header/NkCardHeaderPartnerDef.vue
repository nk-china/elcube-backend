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
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="显示单据编号">
                {{def.docNumberVisible}}
                <a-switch slot="edit" v-model="def.docNumberVisible"></a-switch>
            </nk-form-item>
        </nk-form>
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