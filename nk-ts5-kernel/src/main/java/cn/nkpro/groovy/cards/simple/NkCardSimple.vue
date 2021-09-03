<template>
    <nk-card>
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="字段">
                {{data.field}}
                <a-input v-model="data.field" slot="edit" @change="$nkCalc('123');" />
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
import Mixin from "Mixin";
export default {
    mixins:[new Mixin({field:"test"})],
    created() {
        console.log(this.$docs)
    }
}
</script>

<style scoped>

</style>

<docs>
    - 这是一段文档
</docs>