import type { Config } from "tailwindcss";
const config: Config = {
  content: ["./app/**/*.{js,ts,jsx,tsx,mdx}", "./components/**/*.{js,ts,jsx,tsx}", "./lib/**/*.{js,ts}"],
  theme: {
    extend: {
      colors: {
        nova: { 900: "#0a0a0a", 800: "#171717", 600: "#262626", 500: "#404040" },
        accent: "#ff3b30"
      },
      fontFamily: { sans: ["var(--font-inter)", "system-ui", "sans-serif"], display: ["var(--font-display)", "sans-serif"] }
    }
  },
  plugins: []
};
export default config;
