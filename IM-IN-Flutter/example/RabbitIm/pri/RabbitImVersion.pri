#TODO:发行版本时，需要修改下列值  
MAJOR_VERSION_NUMBER=0       #主版本  
MINOR_VERSION_NUMBER=1       #次版本  
REVISION_VERSION_NUMBER=1    #修订号  

isEmpty(BUILD_VERSION) {
    isEmpty(GIT) : GIT=$$(GIT)
    isEmpty(GIT) : GIT=git
    isEmpty(GIT_DESCRIBE) {
        GIT_DESCRIBE = $$system(cd $$system_path($$PWD) && $$GIT describe --tags)
        isEmpty(BUILD_VERSION) {
            BUILD_VERSION = $$GIT_DESCRIBE
        }
    }
    isEmpty(BUILD_VERSION) {
        BUILD_VERSION = $$system(cd $$system_path($$PWD) && $$GIT rev-parse --short HEAD)
    }
    
    isEmpty(BUILD_VERSION){
        error("Built without git, please add BUILD_VERSION to DEFINES or add git path to environment variable GIT or qmake parameter GIT")
    }
}
message("BUILD_VERSION:$$BUILD_VERSION")
DEFINES *= BUILD_VERSION=\"\\\"$$quote($$BUILD_VERSION)\\\"\"
DEFINES *= RABBITIM_SYSTEM=\"\\\"$$quote($$RABBITIM_SYSTEM)\\\"\"
DEFINES *= RABBITIM_PLATFORM=\"\\\"$$quote($$RABBITIM_PLATFORM)\\\"\"
DEFINES *= RABBITIM_ARCHITECTURE=\"\\\"$$quote($$RABBITIM_ARCHITECTURE)\\\"\"

#更新检测文件  
UPDATE_FILENAME=$$PWD/../Update/Update_$${RABBITIM_SYSTEM}.xml
#更新检测文件模板文件  
TEMPLATE_UPDATE_FILENAME=$$PWD/../Plugin/Update/Update.xml.template
RABBITIM_UPDATE_CONTENTS=$$cat($$TEMPLATE_UPDATE_FILENAME, blob)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_SYSTEM%", $$RABBITIM_SYSTEM)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_PLATFORM%", $$RABBITIM_PLATFORM)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_ARCHITECTURE%", $$RABBITIM_ARCHITECTURE)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%BUILD_VERSION%", $$BUILD_VERSION)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_TIME%", $$RABBITIM_TIME)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_INFO%", $$RABBITIM_INFO)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_FORCE%", $$RABBITIM_FORCE)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_UPDATE_CURL%", $$RABBITIM_UPDATE_CURL)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_MD5SUM%", $$RABBITIM_MD5SUM)
RABBITIM_UPDATE_CONTENTS=$$replace(RABBITIM_UPDATE_CONTENTS, "%RABBITIM_MIN_COMPATIBLE_VERSIOIN%", $$RABBITIM_MIN_COMPATIBLE_VERSIOIN)
#message($$RABBITIM_UPDATE_CONTENTS)
write_file($$UPDATE_FILENAME, RABBITIM_UPDATE_CONTENTS)

#doxygen必须位于的环境变量PATH中  
equals(RABBITIM_USE_DOXYGEN, 1):!android {
    #更新Doxyfile中的版本信息  
    DOXYFILE_FILENAME=$$OUT_PWD/Doxyfile
    TEMPLATE_DOXYFILE_FILENAME=$$PWD/../Doxyfile.template
    RABBITIM_DOXYFILE_CONTENTS=$$cat($$TEMPLATE_DOXYFILE_FILENAME, blob)
    RABBITIM_DOXYFILE_CONTENTS=$$replace(RABBITIM_DOXYFILE_CONTENTS, "%VERSION_NUMBER_STRING%", $$VERSION_NUMBER_STRING)
    RABBITIM_DOXYFILE_CONTENTS=$$replace(RABBITIM_DOXYFILE_CONTENTS, "%INPUT%", $$PWD)
    RABBITIM_DOXYFILE_CONTENTS=$$replace(RABBITIM_DOXYFILE_CONTENTS, "%OUTPUT_DIRECTORY%", $$OUT_PWD/Doxygen)
    #message($$RABBITIM_DOXYFILE_CONTENTS)
    write_file($$DOXYFILE_FILENAME, RABBITIM_DOXYFILE_CONTENTS)
    message("Generate doxygen documents ......")
    DOXYGEN_RETURN=$$system(doxygen $$DOXYFILE_FILENAME)
    #message("DOXYGEN_RETURN:$$DOXYGEN_RETURN")
}