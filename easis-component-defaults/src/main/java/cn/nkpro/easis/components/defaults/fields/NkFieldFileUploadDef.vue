<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="类型">
                {{value.listType}}
                <a-select slot="edit" size="small" v-model="value.listType" >
                    <a-select-option key="text" >文件</a-select-option>
                    <a-select-option key="picture-card" >图片</a-select-option>
                </a-select>
            </nk-form-item>
            <nk-form-item title="Accept">
                {{value.accept}}
                <a-input size="small" slot="edit" v-model="value.accept"></a-input>
            </nk-form-item>
            <nk-form-item title="MaxSize(KB)">
                {{value.maxSize}}
                <a-input-number size="small" slot="edit" v-model="value.maxSize" :max="1024*10"></a-input-number>
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