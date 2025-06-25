export type AppConfig = {
  PUBLIC_API_URL: string;
};

let config: AppConfig | undefined;

export const loadConfig = async (): Promise<void> => {
  const response = await fetch('/config.json');
  if (!response.ok) throw new Error("Failed to load config");
  config = await response.json();
};

export const getConfig = (): AppConfig => {
  if (!config) throw new Error("Config not loaded yet");
  return config;
};
