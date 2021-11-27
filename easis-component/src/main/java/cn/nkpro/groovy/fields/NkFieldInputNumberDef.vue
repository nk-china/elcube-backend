<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="格式">
                {{value.format}}
                <a-input size="small" slot="edit" v-model="value.format"></a-input>
            </nk-form-item>
            <nk-form-item title="最小值">
                {{value.min}}
                <a-input-number size="small" slot="edit" v-model="value.min"></a-input-number>
            </nk-form-item>
            <nk-form-item title="最大值">
                {{value.max}}
                <a-input-number size="small" slot="edit" v-model="value.max"></a-input-number>
            </nk-form-item>
            <nk-form-item title="Digits">
                {{value.digits}}
                <a-input-number slot="edit" size="small" :min="0" :max="6" :precision="0" v-model="value.digits"></a-input-number>
            </nk-form-item>
            <nk-form-item title="Step">
                {{value.step}}
                <a-input-number slot="edit" size="small" :min="0.0001" :max="10000" :precision="2" v-model="value.step"></a-input-number>
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