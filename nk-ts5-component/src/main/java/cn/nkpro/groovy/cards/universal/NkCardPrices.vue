<template>
    <nk-card>
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="字段">
                {{data.field}}
                <a-input v-model="data.field" slot="edit" />
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin({field:undefined})],
        created() {
        }
    }
</script>

<style scoped>

</style>