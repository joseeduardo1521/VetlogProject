package com.example.vet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vet.frag.gestionarMasFragment;
import com.example.vet.frag.registrarMasFragment;

public class ViewPagerAgesMas extends FragmentStateAdapter {

    public ViewPagerAgesMas(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
      switch (position){
          case 0:
              return new registrarMasFragment();
          case 1 :
              return new gestionarMasFragment();
          default:
              return new registrarMasFragment();
      }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
