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
    <nk-def-card>
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="单据默认名称EL" :width="140">
                {{def.docNameDefaultSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.docNameDefaultSpEL" style="max-width:300px;"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="单据名称EL" :width="140">
                {{def.docNameSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.docNameSpEL" style="max-width:300px;"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="伙伴ID EL" :width="140">
                {{def.partnerIdSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerIdSpEL" style="max-width:300px;"></nk-sp-el-editor>

            </nk-form-item>
            <nk-form-item title="伙伴名称EL" :width="140">
                {{def.partnerNameSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerNameSpEL" style="max-width:300px;"></nk-sp-el-editor>
                <span slot="edit" style="color: #aaa">tips: 默认通过伙伴ID EL获取伙伴单据名称；如指定该EL表达式则以该结果作为伙伴名称</span>
            </nk-form-item>
        </nk-form>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";

    export default {
        mixins:[new MixinDef({})],
        computed:{
        },
        created() {
        }
    }
</script>

<style scoped>

</style>