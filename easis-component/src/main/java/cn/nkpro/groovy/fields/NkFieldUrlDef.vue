<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="最大长度">
                {{value.maxLength}}
                <a-input-number size="small" slot="edit" :min="1" :max="100" v-model="value.maxLength"></a-input-number>
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