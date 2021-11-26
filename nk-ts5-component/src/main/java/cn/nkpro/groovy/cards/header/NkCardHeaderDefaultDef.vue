<template>
    <nk-def-card>
        <nk-form :col="1">
            <nk-form-item title="伙伴ID SpEL">
                {{def.partnerIdSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerIdSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="伙伴名称SpEL">
                {{def.partnerNameSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerNameSpEL"></nk-sp-el-editor>
            </nk-form-item>
        </nk-form>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";

    export default {
        mixins:[new MixinDef()],
        computed:{
        },
        created() {
        }
    }
</script>

<style scoped>

</style>