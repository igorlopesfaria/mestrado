package br.ufba.exerciserecognition.listener;

import java.util.List;

/**
 * Created by Igor Lopes de Faria on 16/12/15.
 */
public interface ActionListener {

    void onSynchronizeCompleted(Object object);

    void onSynchronizeCompleted();

    void onSynchronizeCompleted(List<? extends Object> list);

    void onSynchronizeFailed(String message);

}
