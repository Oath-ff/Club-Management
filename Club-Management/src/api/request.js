import apiClient from "./index"; // 引入基础 Axios 实例

class Request {
  /**
   * 发送 GET 请求
   * @param {String} url 请求地址
   * @param {Object} params 请求参数
   * @param {Object} config 额外配置
   * @returns Promise
   */
  get(url, params, config = {}) {
    return apiClient.get(url, { params, ...config });
  }

  /**
   * 发送 POST 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  post(url, data, config = {}) {
    return apiClient.post(url, data, ...config);
  }

  /**
   * 发送 PUT 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  put(url, data, config = {}) {
    return apiClient.put(url, data, ...config);
  }

  /**
   * 发送 PATCH 请求
   * @param {String} url 请求地址
   * @param {Object} data 请求数据
   * @param {Object} config 额外配置
   * @returns Promise
   */
  patch(url, data, config = {}) {
    return apiClient.patch(url, data, ...config);
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
