<!--
	This file is part of ELCard.
	ELCard is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCard is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <nk-form ref="form" size="small" :col="2" :edit="editMode" >
        <nk-form-item v-if="doc" term="交易类型" :col="2">
            {{doc && doc.docType}} | {{doc.def && doc.def.docName}}
        </nk-form-item>
        <nk-form-item term="名称"
                      :validateFor="doc.docName"
                      :required="true"
                      message="请输入交易伙伴名称" :col="2">
            {{doc.docName}}
            <a-input slot="edit" size="small" v-model="doc.docName" allowClear style="max-width: 180px;"></a-input>
        </nk-form-item>
        <nk-form-item term="创建时间">{{doc.createdTime | nkDatetimeFriendly}}</nk-form-item>
        <nk-form-item term="更新时间">{{doc.updatedTime | nkDatetimeFriendly}}</nk-form-item>
        <nk-form-item term="备注" :col="2">
            {{doc.docDesc||'暂无内容'}}
            <a-textarea v-model="doc.docDesc" slot="edit" :auto-size="{ minRows: 3, maxRows: 10 }"></a-textarea>
        </nk-form-item>
    </nk-form>
</template>

<script>
    import {Mixin} from "nk";

    export default {
        mixins:[new Mixin()],
        data(){
            return {
            }
        },
        created() {

        },
        methods:{
            hasError(){
                console.log(this.$refs.form.hasError())
                return this.$refs.form.hasError()
            }
        }
    }
</script>

<style scoped>

</style>