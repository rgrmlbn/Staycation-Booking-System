import NavBar from "./components/layouts/NavBar";
import { Route, Routes } from "react-router-dom";
import Home from "./pages/home/Home";
import UnderConstruction from "./pages/under-construction/UnderConstruction";

function App() {
  return (
    <>
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="*" element={<UnderConstruction />} />
      </Routes>
    </>
  );
}

export default App;
