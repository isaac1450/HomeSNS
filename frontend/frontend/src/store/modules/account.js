import axios from "axios";
import createPersistedState from "vuex-persistedstate";

const login = {
  plugins: [createPersistedState()],
  namespaced: true,
  state: {
    isLogin: localStorage.getItem("jwt") ? true : false,
  },
  mutations: {
    LOGIN: function (state) {
      state.isLogin = true;
    },
  },
  actions: {
    login: function ({ commit }, credentials) {
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_MCS_URL}/login`,
        data: credentials,
      })
        .then((res) => {
          localStorage.setItem("jwt", res.data.token);
          commit("LOGIN");

          console.log(res);
        })
        .catch((err) => {
          console.log(err);
        });
    },
    signup: function ({ commit }, credentials) {
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_MCS_URL}/signup`,
        data: credentials,
      })
        .then((res) => {
          login(credentials);
          console.log(res);
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
  getters: {},
};

export default login;