<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="时间选择">
                {{value.showTime}}
                <a-switch size="small" slot="edit" v-model="value.showTime"></a-switch>
            </nk-form-item>
            <nk-form-item title="显示格式">
                {{value.format}}
                <a-input size="small" slot="edit" v-model="value.format"></a-input>
            </nk-form-item>
            <nk-form-item title="存储格式">
                {{value.formatStorage}}
                <a-input size="small" slot="edit" v-model="value.formatStorage"></a-input>
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