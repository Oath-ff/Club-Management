/**
 * @description pinia 持久化参数配置
 * @param {String} key 存储到持久化的 name
 * @param {Array} paths 需要持久化的 state name
 * @return persist
 * */
const piniaPersistConfig = (key, paths) => {
  const persist = {
    key,
    storage: localStorage, // 使用 localStorage 来持久化状态
    //storage: sessionStorage, // 使用 sessionStorage
    paths: Array.isArray(paths) ? paths : [paths]
  };
  return persist;
};

export default piniaPersistConfig;
