<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="对话框选项">
                {{value.modelConfig}}
                <nk-sp-el-template-editor slot="edit" v-model="value.options"></nk-sp-el-template-editor>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object
    }
}
</script>

<style scoped>

</style>