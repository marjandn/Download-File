package dnejad.marjan.downloadfile;

import android.support.v4.content.FileProvider;

/**
 * Created by Marjan.Dnejad
 * on 2/22/2018.
 *
 *
 *
 *
 *
 *
 * If your targetSdkVersion >= 24, then we have to use FileProvider class
 * to give access to the particular file or folder to make them accessible for other apps.
 * We create our own class inheriting FileProvider
 * in order to make sure our FileProvider doesn't conflict with FileProviders declared in imported dependencies
 *
 * for more info check this page
 * https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
 */

public class GenericFileProvider extends FileProvider {
}
