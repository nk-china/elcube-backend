<template>
    <nk-dynamic-form :edit-mode="editMode" :create-mode="createMode" :doc="doc" :card="card"></nk-dynamic-form>
</template>

<script>
    import {Mixin, NkDynamicForm} from 'nk';
    export default {
        mixins:[new Mixin()],
        components:{
            NkDynamicForm
        }
    }
</script>

<style scoped>

</style>