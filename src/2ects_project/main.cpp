#include "mainwindow.h"
#include "extlibs/nlohmann/json.hpp" // < will underline a warning like "header is not used directly - well, if it builds, leave it that way
#include <QApplication>
#include <iostream>

int main(int argc, char *argv[])
{
    try {
        nlohmann::json json = nlohmann::json::parse("");
    }
    catch (...) {
        std::cout << "parsing did not work :(" << std::endl;
    }

    QApplication a(argc, argv);
    MainWindow w;
    w.show();
    return a.exec();
}
