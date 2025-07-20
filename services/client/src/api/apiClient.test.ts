import { apiClient, apiClientPromise } from "./apiClient";

jest.mock("openapi-fetch", () => {
  const use = jest.fn();
  const createClient = jest.fn(() => ({
    use,
  }));
  return { __esModule: true, default: createClient };
});

jest.mock("../utils/storage", () => ({
  getItem: jest.fn(),
}));

describe("apiClient", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.resetModules();
  });

  it("uses PUBLIC_API_URL from config.json if available", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ PUBLIC_API_URL: "https://api.example.com/" }),
    }) as any;

    // Import after mocks and resetModules
    const { apiClientPromise } = await import("./apiClient");
    const createClient = require("openapi-fetch").default;

    await apiClientPromise;
    expect(createClient).toHaveBeenCalledWith({ baseUrl: "https://api.example.com/" });
  });

  it("falls back to default baseUrl if config.json not found", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
    }) as any;

    const { apiClientPromise } = await import("./apiClient");
    const createClient = require("openapi-fetch").default;

    await apiClientPromise;
    expect(createClient).toHaveBeenCalledWith({ baseUrl: "http://localhost:8080/api/v1/" });
  });

  it("sets Authorization header if jwtToken is present", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
    }) as any;

    const storage = require("../utils/storage");
    storage.getItem.mockReturnValue("mytoken");

    const { apiClientPromise } = await import("./apiClient");
    const createClient = require("openapi-fetch").default;

    await apiClientPromise;

    // Simulate the onRequest interceptor
    const clientInstance = createClient.mock.results[0].value;
    const interceptor = clientInstance.use.mock.calls[0][0];
    const request = { headers: { set: jest.fn() } };
    await interceptor.onRequest({ request });

    expect(request.headers.set).toHaveBeenCalledWith("Authorization", "Bearer mytoken");
  });

  it("does not set Authorization header if jwtToken is missing", async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
    }) as any;

    const storage = require("../utils/storage");
    storage.getItem.mockReturnValue(undefined);

    const { apiClientPromise } = await import("./apiClient");
    const createClient = require("openapi-fetch").default;

    await apiClientPromise;

    const clientInstance = createClient.mock.results[0].value;
    const interceptor = clientInstance.use.mock.calls[0][0];
    const request = { headers: { set: jest.fn() } };
    await interceptor.onRequest({ request });

    expect(request.headers.set).not.toHaveBeenCalled();
  });
});