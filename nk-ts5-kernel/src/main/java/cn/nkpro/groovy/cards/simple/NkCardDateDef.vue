<template>
    <a-card title="日期配置2">
        这是一个后端写的Vue文件
        <a-input v-model="def.field"></a-input>
    </a-card>
</template>

<script>
    import MixinDef from 'MixinDef';
    export default {
        mixins:[new MixinDef({field:"test"})],
        created() {
        }
    }
</script>

<style>

</style>