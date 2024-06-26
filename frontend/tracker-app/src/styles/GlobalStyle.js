import { createGlobalStyle } from "styled-components";
import reset from "styled-reset";
import { theme } from "./theme";
const GlobalStyle = createGlobalStyle`
  ${reset}
  #root{
    font-family: ${theme.typography.fontFamily}
  }
  *{
    margin: 0;
    padding: 0;
    border: 0;
    vertical-align: baseline;
    box-sizing: border-box;
    text-align: center;
  a{
    text-decoration: none;
    color: inherit;
  }
  p{
    line-height: 1.3rem;
  }
  html {
  font-size: 62.5%; // 1rem = 10px
  height: 100%;
  }
  }
  ol, ul, li{
    list-style: none;
  }
  body{
    line-height: 1.2rem;
  }
  article,
  aside,
  details,
  figcaption,
  figure,
  footer,
  header,
  hgroup,
  menu,
  nav,
  section {
  display: block;
  }
`;

export default GlobalStyle;
