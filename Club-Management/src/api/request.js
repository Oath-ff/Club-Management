import apiClient from "./index"; // 引入基础 Axios 实例

class Request {
  /**
   * 发送 GET 请求
   * @param {String} url 请求地址
   * @param {Object} params 请求参数
   * @param {Object} config 额外配置
   * @returns Promise
   */
  get(url, params = {}, config = {}) {
    if (Object.keys(params).length === 0 && Object.keys(config).length === 0) {
      return apiClient.get(url); // 如果 params 和 config 都为空，直接发送 URL 请求
    }
    if (Object.keys(params).length === 0) {
      return apiClient.get(url, config); // 如果 params 为空，但 config 不为空，发送 URL 和 config
    }
    return apiClient.get(url, { params, ...config }); // 如果 params 不为空，发送包含 params 和 config 的请求
  }

  /**
   * 发送 POST 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  post(url, data, config = {}) {
    return apiClient.post(url, data, config);
  }

  /**
   * 发送 PUT 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  put(url, data, config = {}) {
    return apiClient.put(url, data, config);
  }

  /**
   * 发送 PATCH 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  patch(url, data, config = {}) {
    return apiClient.patch(url, data, config);
  }

  /**
   * 发送 DELETE 请求
   * @param {String} url 请求地址
   * @param {Object} params 请求参数
   * @param {Object} config 额外配置
   * @returns Promise
   */
  delete(url, params, config = {}) {
    return apiClient.delete(url, { params, ...config });
  }
}

export default new Request();
