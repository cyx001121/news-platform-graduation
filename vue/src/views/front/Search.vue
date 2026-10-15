<template>
  <div class="main-content">
<blog-list />
  </div>
</template>

<script>
import BlogList from "@/components/BlogList";
export default {
  name: "Search",
  components: {BlogList},
  data(){
    return{
      title:this.$route.query.title
    }
  },

}
</script>

<style scoped>

</style>