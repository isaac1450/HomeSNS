<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="400">
      <v-card>

        <v-card-title class="headline">{{ event.scheduleTitle }}</v-card-title>
        <v-card-text style="font-size: 1rem" class="font-weight-bold">{{ event.scheduleContent }}</v-card-text>

        <div class="ml-5 font-weight-light">
          <div>시작일: {{ getEventStart() }}</div>
          <div>종료일: {{ getEventEnd() }}</div>
        </div>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="close()">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
export default {
  name: "EventDetail",
  computed: {
    dialog() {
      return this.$store.state.calendar.eventDetailDialog;
    },
    event() {
      return this.$store.state.calendar.event;
    }
  },
  methods: {
    getEventStart() {

      return this.event.scheduleDateStart + getTime(this.event.scheduleTimeStart);
    },
    getEventEnd() {
      return this.event.scheduleDateEnd + getTime(this.event.scheduleTimeEnd);
    },
    close() {
      return this.$store.commit('CLOSE_EVENT_DETAIL');
    }
  }
}

const getTime = (time) => {
  return time === null ? '' : ` ${time}`;
};
</script>