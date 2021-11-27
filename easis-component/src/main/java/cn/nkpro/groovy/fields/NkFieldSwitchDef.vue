<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="选中后显示">
                {{value.checked}}
                <a-input slot="edit" size="small" :maxLength="20" v-model="value.checked"></a-input>
            </nk-form-item>
            <nk-form-item title="未选中显示">
                {{value.unChecked}}
                <a-input slot="edit" size="small" :maxLength="20" v-model="value.unChecked"></a-input>
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