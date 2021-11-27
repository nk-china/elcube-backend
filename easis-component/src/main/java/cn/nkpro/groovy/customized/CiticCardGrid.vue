<template>
    <nk-dynamic-grid :edit-mode="editMode" :create-mode="createMode" :doc="doc" :card="card"></nk-dynamic-grid>
</template>

<script>
    import {Mixin, NkDynamicGrid} from 'nk';
    export default {
        mixins:[new Mixin()],
        components:{
            NkDynamicGrid
        }
    }
</script>

<style scoped>

</style>