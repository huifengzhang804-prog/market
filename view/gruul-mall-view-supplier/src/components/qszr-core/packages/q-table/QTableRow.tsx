import { defineComponent } from 'vue'

export default defineComponent({
    setup(prop, { slots }) {
        return () => <tr class="body--content">{slots.default && slots.default()}</tr>
    },
})
