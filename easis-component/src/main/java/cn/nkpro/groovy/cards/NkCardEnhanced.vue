<template>
    <nk-card>
        <div class="markdown" v-html="markdown" />
    </nk-card>
</template>

<script>
import Mixin from "Mixin";
import MarkdownIt from 'markdown-it';

export default {
    mixins:[Mixin()],
    data(){
        return {
            markdown:undefined
        }
    },
    created() {
        this.markdown = new MarkdownIt().render(this.card.markdown);
    }
}
</script>
