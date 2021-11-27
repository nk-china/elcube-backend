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
    import {Mixin} from "nk-ts5-platform";

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