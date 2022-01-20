import Vue from "vue";
import Vuex from "vuex";
import login from "./modules/login";
import getMain from "./modules/getMain";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  plugins: [createPersistedState()],
  state: {},
  mutations: {},
  actions: {},
  modules: {
    login,
    getMain,
  },
});
