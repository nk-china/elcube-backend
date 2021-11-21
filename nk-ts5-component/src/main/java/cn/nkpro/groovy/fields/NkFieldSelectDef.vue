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
<!--            <nk-form-item title="Min" v-if="options.$options.min!==undefined">-->
<!--                {{row.min}}-->
<!--                <a-input-number slot="edit" size="small" v-model="options.min"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="Max" v-if="options.$options.max">-->
<!--                {{row.max}}-->
<!--                <a-input-number slot="edit" size="small" v-model="options.max"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="MinLength" v-if="options.$options.minLength">-->
<!--                {{row.minLength}}-->
<!--                <a-input-number slot="edit" size="small" :min="0" :max="200" :precision="0" v-model="options.minLength"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="MaxLength" v-if="options.$options.maxLength">-->
<!--                {{row.maxLength}}-->
<!--                <a-input-number slot="edit" size="small" :min="1" :max="200" :precision="0" v-model="options.maxLength"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="Digits" v-if="options.$options.digits">-->
<!--                {{row.digits}}-->
<!--                <a-input-number slot="edit" size="small" :min="0" :max="6" :precision="0" v-model="options.digits"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="Step" v-if="options.$options.step">-->
<!--                {{row.step}}-->
<!--                <a-input-number slot="edit" size="small" :min="0.0001" :max="10000" :precision="2" v-model="options.step"></a-input-number>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="选中后显示" v-if="options.$options.checked">-->
<!--                {{row.checked}}-->
<!--                <a-input slot="edit" size="small" :maxLength="20" v-model="options.checked"></a-input>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="未选中显示" v-if="options.$options.unChecked">-->
<!--                {{row.unChecked}}-->
<!--                <a-input slot="edit" size="small" :maxLength="20" v-model="options.unChecked"></a-input>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="正则表达式" v-if="options.$options.pattern !== undefined">-->
<!--                {{row.pattern}}-->
<!--                <a-input slot="edit" size="small" :maxLength="20" v-model="options.pattern"></a-input>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="对话框" v-if="options.$options.modal !== undefined">-->
<!--                {{row.modal}}-->
<!--                <nk-doc-select-editor slot="edit" v-model="options.modal"></nk-doc-select-editor>-->
<!--            </nk-form-item>-->
<!--            <nk-form-item title="显示格式" v-if="options.$options.format">-->
<!--                {{row.format}}-->
<!--                <a-input slot="edit" size="small" v-model="options.format"></a-input>-->
<!--            </nk-form-item>-->
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object,
    },
    data(){
        return {}
    },
    computed:{
    },
    methods:{
    }
}
</script>

<style scoped>

</style>