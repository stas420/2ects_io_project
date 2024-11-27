#include "mainwindow.h"
#include <QApplication>
#include "extlibs/nlohmann/json.hpp"
#include <iostream>
#include "extlibs/curl-8.11.0_3-win64-mingw/include/curl/curl.h"

int main(int argc, char *argv[])
{
    /*
     * this should probably compile and build an empty window
     * all below "empty declarations" and no-sense actions
     * are purely for test purposes
     */

    CURL *curl; //< should work fine

    try {
        nlohmann::json json = nlohmann::json::parse(""); //< should work fine
    }
    catch (...) {
        std::cout << "parsing did not work :(" << std::endl;
    }

    QApplication a(argc, argv);
    MainWindow w;
    w.show();
    return a.exec();
}
