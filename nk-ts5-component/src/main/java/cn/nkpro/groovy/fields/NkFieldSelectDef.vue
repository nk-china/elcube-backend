<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="选项表达式">
                {{value.options}}
                <nk-sp-el-template-editor slot="edit" v-model="value.options"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="选择模式">
                {{value.selectMode}}
                <a-select slot="edit" size="small" v-model="value.selectMode" mode="default" >
                    <a-select-option key="default">default</a-select-option>
                    <a-select-option key="multiple">multiple</a-select-option>
                    <a-select-option key="tags" disabled>tags</a-select-option>
                </a-select>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object,
    }
}
</script>

<style scoped>

</style>