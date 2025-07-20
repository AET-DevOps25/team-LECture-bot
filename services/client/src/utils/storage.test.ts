import storage from "./storage";

describe("storage utility", () => {
  beforeEach(() => {
    // @ts-ignore
    global.window = Object.create(window);
    Object.defineProperty(window, "localStorage", {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
      },
      writable: true,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("getItem returns parsed value if present", () => {
    window.localStorage.getItem = jest.fn(() => JSON.stringify({ foo: "bar" }));
    const result = storage.getItem<{ foo: string }>("key");
    expect(result).toEqual({ foo: "bar" });
    expect(window.localStorage.getItem).toHaveBeenCalledWith("key");
  });

  it("getItem returns null if not present", () => {
    window.localStorage.getItem = jest.fn(() => null);
    const result = storage.getItem("missing");
    expect(result).toBeNull();
  });

  it("getItem returns null and warns on JSON parse error", () => {
    const warn = jest.spyOn(console, "warn").mockImplementation(() => {});
    window.localStorage.getItem = jest.fn(() => "{notjson");
    const result = storage.getItem("bad");
    expect(result).toBeNull();
    expect(warn).toHaveBeenCalled();
    warn.mockRestore();
  });

  it("setItem stringifies and stores value", () => {
    window.localStorage.setItem = jest.fn();
    storage.setItem("key", { foo: "bar" });
    expect(window.localStorage.setItem).toHaveBeenCalledWith("key", JSON.stringify({ foo: "bar" }));
  });

  it("setItem warns on error", () => {
    const warn = jest.spyOn(console, "warn").mockImplementation(() => {});
    window.localStorage.setItem = jest.fn(() => { throw new Error("fail"); });
    storage.setItem("key", { foo: "bar" });
    expect(warn).toHaveBeenCalled();
    warn.mockRestore();
  });

  it("removeItem removes the key", () => {
    window.localStorage.removeItem = jest.fn();
    storage.removeItem("key");
    expect(window.localStorage.removeItem).toHaveBeenCalledWith("key");
  });

  it("removeItem warns on error", () => {
    const warn = jest.spyOn(console, "warn").mockImplementation(() => {});
    window.localStorage.removeItem = jest.fn(() => { throw new Error("fail"); });
    storage.removeItem("key");
    expect(warn).toHaveBeenCalled();
    warn.mockRestore();
  });
});